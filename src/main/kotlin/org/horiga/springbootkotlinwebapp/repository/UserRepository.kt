/*
 * Copyright (c) 2017 LINE Corporation. All rights reserved.
 * LINE Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.horiga.springbootkotlinwebapp.repository

import org.horiga.springbootkotlinwebapp.handler.User

interface UserRepository {
    fun getAll(): Collection<User>
    fun get(id: String) : User
    fun add(user: User)
}

class OnMemoryUserRepository : UserRepository {

    val users: MutableMap<String, User> = mutableMapOf()

    override fun get(id: String): User {
        return users.get(id) ?: throw Exception("undefined user. id=$id")
    }

    override fun add(user: User) {
        users.put(user.id, user)
    }

    override fun getAll(): Collection<User> {
        return users.values.toList()
    }
}