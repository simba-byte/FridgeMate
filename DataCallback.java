package com.example.fridgemate.repository;

public interface DataCallback<T> {
    // 简单回调接口：Repository 在后台线程取到数据后交还给页面层。
    void onData(T data);
}
