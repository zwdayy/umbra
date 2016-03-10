package com.umbra.cache.factory;

/**
 * Created by zhangweiding on 15/9/18.
 */
public interface ILimitedFactory<Key,Val> {


    /**
     * element元素被加入缓存的时候回调
     * @param key
     *         element元素的key
     * @param val
     *         element元素的value
     */
    void onPut(Key key, Val val);

    /**
     * element元素被get一次触发的回调
     * @param key
     *          获取到element元素的key
     * @param val
     *          获取到element元素的value
     */
    void onGet(Key key, Val val);

    /**
     * 有element从缓存中移除是回调
     * @param key
     *          移除element的key
     * @param val
     *          移除element的Value
     */
    void onRemove(Key key, Val val);

    /**
     * 检查当前要被remove的element元素
     * @return
     *         返回当前要被移除element元素的值
     */
    Key onCheckRemoveKey();
}
