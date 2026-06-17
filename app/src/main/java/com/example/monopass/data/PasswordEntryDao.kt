package com.example.monopass.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PasswordEntryDao {
    @Query("SELECT * FROM password_entries ORDER BY name ASC")
    fun getAll(): LiveData<List<PasswordEntry>>

    @Query("SELECT * FROM password_entries WHERE id = :id")
    suspend fun getById(id: Int): PasswordEntry?

    @Insert
    suspend fun insert(entry: PasswordEntry)

    @Update
    suspend fun update(entity: PasswordEntry)

    @Delete
    suspend fun delete(entry: PasswordEntry)
}