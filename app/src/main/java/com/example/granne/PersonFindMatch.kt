package com.example.granne

data class PersonFindMatch(
    val name: Any? = null,
    val aboutMe: Any? = null,
    val intressen: MutableCollection<Any>? = null,
    var uid: Any? = null,
    val buttonAdd: Any? = null, // why
    val image: Int? = R.drawable.userim, // why
) {
}