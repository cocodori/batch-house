package com.fastcampus.housebatch.adapter

import org.springframework.stereotype.Service

@Service
class FakeSendService: SendService {
    override fun send(email: String, message: String) {
        println("email: $email \nmessage: $message")
    }
}