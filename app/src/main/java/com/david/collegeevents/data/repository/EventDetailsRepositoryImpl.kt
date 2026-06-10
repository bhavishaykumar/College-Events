package com.david.collegeevents.data.repository

import com.david.collegeevents.data.remote.ApiServices
import com.david.collegeevents.data.remote.dto.GenericErrorDto
import com.david.collegeevents.domain.model.EventDetail
import com.david.collegeevents.domain.repository.EventDetailsRepository
import com.david.collegeevents.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EventDetailsRepositoryImpl @Inject constructor(
    private val api: ApiServices
) : EventDetailsRepository {

    override fun getEventDetail(eventId: String): Flow<Resource<EventDetail>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getEventDetail(eventId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                emit(Resource.Success(
                    EventDetail(
                        id = body.id, title = body.title, clubName = body.clubName,
                        bannerUrl = body.bannerUrl, date = body.date, time = body.time,
                        venue = body.venue, description = body.description,
                        seatAvailability = body.seatAvailability, registrationFee = body.registrationFee,
                        isUserRegistered = body.isUserRegistered
                    )
                ))
            } else {
                emit(Resource.Error("Failed to fetch event breakdown data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Check backend link setup connection"))
        }
    }

    override fun toggleRegistration(eventId: String, register: Boolean): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = if (register) api.registerForEvent(eventId) else api.deregisterFromEvent(eventId)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.message))
            } else {
                val err = Gson().fromJson(response.errorBody()?.string(), GenericErrorDto::class.java).message
                emit(Resource.Error(err))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Operation transaction failed processing downstream"))
        }
    }
}