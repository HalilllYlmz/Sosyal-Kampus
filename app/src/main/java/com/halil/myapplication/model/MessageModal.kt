package com.halil.myapplication.model

data class MessageModal(val message: String, val from: String, val to: String, val receiverName: String,
                        val chatImageURL: String, val time: String)