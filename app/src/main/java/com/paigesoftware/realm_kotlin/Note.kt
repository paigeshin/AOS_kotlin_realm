package com.paigesoftware.realm_kotlin

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Note(
    @PrimaryKey
    var id: Int? = null,
    var title: String? = null,
    var description: String? = null
): RealmObject()
