package com.dictionaryapp.objects

data class DictionaryEntity(
    var dictId_notOnline: String,
    var dictName: String,
    var owner_uid: String,
    var owner_nickname: String,
    var words: ArrayList<Word>,
    var lang1: String,
    var lang2: String,
    var public: Boolean,
    var timestampLastView: Long,
    var number_of_copies: Long
) {
    constructor() : this("-1","","-1","Unknown", ArrayList(), "Other", "Other", false,0,1)

}