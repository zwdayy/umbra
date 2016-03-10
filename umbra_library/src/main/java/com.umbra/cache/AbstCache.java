package com.umbra.cache;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.umbra.cache.factory.FIFOLimitedFactory;
import com.umbra.cache.factory.ILimitedFactory;
import com.umbra.util.IOUtil;
import com.umbra.util.StorageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by zhangweiding on 15/9/18.
 */

public abstract class AbstCache<Key, Val> {

	private static final String TAG = "[AbstCache]";

	/**
	 * 缓存的容量
	 */
	private int mCapacity;

	protected String destCacheDir;

	private Map<Key, Val> cache ;

	/**
	 * Disk中缓存的有效期
	 */
	private long cacheSaveInMinutes ;

	private ILimitedFactory<Key, Val> mLimitFac;

	/**
	 * 当前使用的缓存大小
	 */
	private AtomicInteger mCacheSize = new AtomicInteger(0);

	private ILimitedFactory<Key, Val> mDefaultFac = new FIFOLimitedFactory<Key, Val>();

	/**
	 *
	 * @param context
	 * 			application context对象
	 * @param capacity
	 * 			构建缓存的容量大小
	 * @param destDir
	 * 			缓存的存放目录  eg:"image","byte"
	 * @param saveInMinutes
	 * 			Disk中缓存的有效期
	 */

	public AbstCache(Context context,int capacity,String destDir,long saveInMinutes){
		mCapacity = capacity;
		cacheSaveInMinutes = saveInMinutes;
		cache = new ArrayMap<Key,Val>();
		destCacheDir = getCacheDir(context,destDir);
		sanitizeDiskCache();
	}

	/**
	 * 判断是否可以缓存至磁盘（sdcard、phone）
	 *
	 * @param context
	 * @param destDir
	 *            缓存的文件目录
	 *
	 * @returnß
	 */
	private String getCacheDir(Context context,String destDir) {
		File tmp = StorageUtil.getCacheDirectory(context);
		if(tmp == null) {
			return null;
		}
		tmp = new File(tmp,destDir);
		if(tmp.exists()) {
			return tmp.getAbsolutePath();
		}
		if(tmp.mkdirs()){
			return  tmp.getAbsolutePath();
		}
		return null;
	}


	/**
	 * 获取缓存路径
	 * */
	public String getDiskCacheDirectory() {
		return destCacheDir;
	}


	public synchronized Val get(Key key) {
		Val value = getFromMemery(key);
		if(value == null){
			value = getFromDisk(key);
			if (value != null) {
				putInMemery(key, value);
			}
		}
		return value;
	}


	/**
	 * 判断某个key是否在有效期内
	 * */
	public synchronized Val getFromMemery(Key key) {
		Val value = cache.get(key);
		if (value != null) {
			getLimitedFactory1().onGet(key, value);
		}
		return value;
	}


	/**
	 * 根据key获取持久化缓存文件
	 * */
	private synchronized File getFileForKey(Key key) {
		if(TextUtils.isEmpty(destCacheDir)){
			return null;
		}
		File outFile = new File(destCacheDir);
		if (!outFile.exists()) {
			outFile.mkdirs();
		}
		return new File(outFile, getFileNameForKey(key));
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public synchronized Val getFromDisk(Key key) {
		if(TextUtils.isEmpty(destCacheDir)){
			return null;
		}
		File file = getFileForKey(key);
		Val value = null;
		if (file.exists()) {
			try {
				value = readValueFromDisk(file);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return value;
	}


	public synchronized boolean put(Key key, Val value) {
		return put(key, value, null);
	}

	public synchronized boolean put(Key key, Val value, byte[] data) {
		return put(key, value, data, 0, data == null ? 0 : data.length);
	}

	/**
	 * 将值置入缓存,如果磁盘可用的话需
	 * Override writeValueToDisk 方法
	 * 重复put不会刷新Disk中的缓存，
	 * @param key		缓存到key
	 * @param value		缓存对象
	 * @param data		缓存对象的数据
	 * @param offset	缓存对象在数据中的起始位置
	 * @param length	缓存对象在数据的数据长度
	 * @return
	 * 		只要在内存中构建缓存成功，就返回true，不考虑在Disk中的返回结果
	 */
	public synchronized boolean put(Key key, Val value, byte[] data, int offset,
									int length) {
		if(putInMemery(key, value)){
			cacheToDisk(key, value, data, offset, length);
			return true;
		}
		return false;
	}

	public synchronized boolean update(Key key, Val value) {
		delete(key);
		return put(key, value, null, 0, 0);
	}

	/**
	 * 缓存更新使用该方法
	 * @param key
	 * @param value
	 * @param data
	 * @param start
	 * @param length
	 * @return
	 */
	public synchronized boolean update(Key key, Val value, byte[] data, int start,
									   int length) {
		delete(key);
		return put(key,value,data,start,length);
	}

	/**
	 * 将值置入Map缓存
	 * */
	public synchronized boolean putInMemery(Key key, Val value) {
		boolean result = false;
		int valueSize = this.getSize(value);
		int curCacheSize = mCacheSize.get();
		if (valueSize < mCapacity) {
			while (true) {
				if (curCacheSize + valueSize <= mCapacity) {
					this.mCacheSize.addAndGet(valueSize);
					result = true;
					break;
				}
				Key remKey = getLimitedFactory1().onCheckRemoveKey();
				if (remKey != null) {
					Val remVal = remove(remKey);
					curCacheSize = mCacheSize.addAndGet(-this.getSize(remVal));
				}
			}
		} else {
			return result;
		}
		cache.put(key, value);
		getLimitedFactory1().onPut(key, value);
		return result;
	}

	/**
	 * 从map缓存中移除key
	 * */
	public synchronized Val remove(Key key) {
		WeakReference<Key> keyReference = new WeakReference<Key>(
				key);
		Val value = cache.remove(key);
		WeakReference<Val> valueReference = new WeakReference<Val>(
				value);
		if (value != null) {
			getLimitedFactory1().onRemove(keyReference.get(), valueReference.get());
		}
		return value;
	}


	/**
	 * 从磁盘缓存与map缓存中删除key
	 * */
	public synchronized Val delete(Key key) {
		if (!TextUtils.isEmpty(destCacheDir)) {
			File file = getFileForKey(key);
			if (file != null && file.exists()) {
				file.delete();
			}
		}
		return remove(key);
	}


	private ILimitedFactory<Key, Val> getLimitedFactory1() {
		return mLimitFac == null ? mDefaultFac : mLimitFac;
	}


	public void setILimitedFactory(ILimitedFactory<Key,Val> limitedFactory){
		this.mLimitFac = limitedFactory;
	}

	public ILimitedFactory<Key,Val> getIlimitedFactory(){
		return mLimitFac;
	}


	/**
	 * 判断key是否存在，不分磁盘与map
	 *
	 * @param key
	 * @return
	 */
	public synchronized boolean containsKey(Key key) {
		File file;
		return cache.containsKey(key) || ((file = getFileForKey(key)) != null && file.exists());
	}

	/**
	 * 判断key是否存在map缓存中 Checks if a value is present in the in-memory cache.
	 * ignores the disk cache
	 *
	 * @param key
	 *            the cache key
	 * @return true if the value is currently hold in memory false otherwise
	 */
	public synchronized boolean containsKeyInMemory(Key key) {
		return cache.containsKey(key);
	}

	/**
	 * 判断在map缓存中是否存在这个值
	 */
	public synchronized boolean containsValueInMemory(Val value) {
		return cache.containsValue(value);
	}


	protected abstract int getSize(Val val);

	/**
	 * 子类实现 ，根据key获取缓存持久化的文件名
	 * */
	protected abstract String getFileNameForKey(Key key);

	/**
	 * 子类实现 ，根据文件对象获取缓存值
	 * */
	protected abstract Val readValueFromDisk(File file) throws IOException;

	/**
	 * 读取文件对象
	 * */
	protected byte[] readByteFromDisk(File file) throws IOException {
		if (file == null || file.length() == 0) {
			return null;
		}
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			throw new IOException("Cannot read files larger than "
					+ Integer.MAX_VALUE + " bytes");
		}
		BufferedInputStream is = null;
		try{
			is = new BufferedInputStream(
					new FileInputStream(file));
			byte[] data = IOUtil.toByteArray(is);
			return data;
		}catch (IOException e){
			throw  e;
		}finally {
			IOUtil.closeStream(is);
		}
	}

	/**
	 * 子类实现 ，将缓存持久化至文件
	 * */
	protected abstract void writeValueToDisk(File file, Val value)
			throws IOException;


	protected void writeByteToDisk(File file, byte[] data, int offset,
								   int length) {
		if (data == null || data.length == 0) {
			return;
		}
		if(!StorageUtil.checkDeviceHasEnghStorage(file, length)){
			return;
		}
		BufferedOutputStream ostream = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ostream = new BufferedOutputStream(fos);
			ostream.write(data, offset, length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeStream(ostream);
		}
	}


	public synchronized boolean cacheToDisk(Key key, Val value, byte[] data, int offset,
											int length) {
		// 文件已经存在，直接返回
		File file = getFileForKey(key);
		if(file == null){
			return false;
		}
		if (file.exists()) {
			return true;
		}
		try {
			// 创建新文件
			if (file.createNewFile()) {
				if (data != null && data.length > 0) {
					writeByteToDisk(file, data, offset, length);
				} else {
					writeValueToDisk(file, value);
				}
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	public synchronized Set<Key> keySet() {
		return cache.keySet();
	}

	public synchronized Set<Entry<Key, Val>> entrySet() {
		return cache.entrySet();
	}


	public synchronized int size() {
		return cache.size();
	}


	public synchronized boolean isEmpty() {
		return cache.isEmpty();
	}

	/**
	 * 清空map缓存
	 * */
	public synchronized void clear() {
		for (Entry<Key, Val> entry : cache.entrySet()) {
			WeakReference<Key> keyReference = new WeakReference<Key>(
					entry.getKey());
			WeakReference<Val> valueReference = new WeakReference<Val>(
					entry.getValue());
			Key key = keyReference.get();
			if (key != null) {
				cache.remove(key);
			}
		}
	}


	/**
	 * 获取map缓存的所有值
	 * */
	public synchronized Collection<Val> values() {
		return cache.values();
	}


	/**
	 * 清理已过期的缓存文件
	 */
	public synchronized void sanitizeDiskCache() {
		new Thread(){
			@Override
			public void run() {
				if(TextUtils.isEmpty(destCacheDir)){
					return;
				}
				File[] cachedFiles = new File(destCacheDir).listFiles();
				if(cachedFiles != null){
					for (File f : cachedFiles) {
						long lastModified = f.lastModified();
						Date now = new Date();
						long ageInMinutes = (now.getTime() - lastModified) / (1000 * 60);
						if (ageInMinutes >= cacheSaveInMinutes) {
							f.delete();
						}
					}
				}
			}
		}.start();

	}



}
