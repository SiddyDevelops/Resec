package com.siddydevelops.sms_kotlin.utils.actions

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

@SuppressLint("Range")
class GetContacts(context: Context,aplhaList: Char,contact: String,phoneNumber: String) {

    private val regex = Regex(".*[a-zA-Z]+.*")
    private val alphaListIN: Char
    private val contactName: String
    private var contactNumber: String

    private val cr: ContentResolver = context.contentResolver
    private val cur: Cursor? = cr.query(
        ContactsContract.Contacts.CONTENT_URI,
        null, null, null, null
    )

    private var contactsMap: HashMap<String, String> = HashMap()
    private var sortedContactsMap: TreeMap<String, String> = TreeMap()
    private var alphaContactsMap: TreeMap<String, String> = TreeMap()

    init {
        alphaListIN = aplhaList
        contactName = contact
        contactNumber = ""
        GlobalScope.launch(Dispatchers.IO) {
            if ((cur?.count ?: 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    val id: String = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)
                    )
                    val name: String = cur.getString(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME
                        )
                    )
                    if (cur.getInt(
                            cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER
                            )
                        ) > 0
                    ) {
                        val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        while (pCur?.moveToNext()!!) {
                            val phoneNo: String = pCur.getString(
                                pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            if(name.matches(regex)) {
                                contactsMap[name] = phoneNo
                            }
                        }
                        pCur.close()
                    }
                }
                sortedContactsMap.putAll(contactsMap)
                if(alphaListIN != '0' || contactName != "0") {
                    for((key, value) in sortedContactsMap) {
                        if(key == contactName) {
                            contactNumber = value
                        }
                        if(key[0] == alphaListIN || key[0] == alphaListIN.lowercaseChar()) {
                            alphaContactsMap[key] = value
                        }
                    }
                }
                Log.d("ContactsMap:", contactsMap.toString())
                Log.d("SortedContactsMap:", sortedContactsMap.toString())
                Log.d("AlphaContactsMap:", alphaContactsMap.toString())
                Log.d("ContactName:", contactName)
                Log.d("ContactNumber:", contactNumber)

            }
            cur?.close()
        }
    }
}