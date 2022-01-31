package com.fastcampus.housebatch.adapter

interface SendService {
    fun send(email: String, message: String)
}