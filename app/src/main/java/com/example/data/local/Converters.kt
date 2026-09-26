package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ConsumerType
import com.example.data.model.DonationStatus
import com.example.data.model.DonorType
import com.example.data.model.InstitutionSubtype
import com.example.data.model.MealPeriod
import com.example.data.model.NotificationType
import com.example.data.model.RequestStatus
import com.example.data.model.TransportPreference
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole?): String? = value?.name
    @TypeConverter
    fun toUserRole(value: String?): UserRole? = value?.let { enumValueOf<UserRole>(it) }

    @TypeConverter
    fun fromDonorType(value: DonorType?): String? = value?.name
    @TypeConverter
    fun toDonorType(value: String?): DonorType? = value?.let {
        try { enumValueOf<DonorType>(it) } catch (_: Exception) { DonorType.INSTITUTIONAL }
    }

    @TypeConverter
    fun fromInstitutionSubtype(value: InstitutionSubtype?): String? = value?.name
    @TypeConverter
    fun toInstitutionSubtype(value: String?): InstitutionSubtype? = value?.let {
        try { enumValueOf<InstitutionSubtype>(it) } catch (_: Exception) { InstitutionSubtype.COLLEGE }
    }

    @TypeConverter
    fun fromConsumerType(value: ConsumerType?): String? = value?.name
    @TypeConverter
    fun toConsumerType(value: String?): ConsumerType? = value?.let { enumValueOf<ConsumerType>(it) }

    @TypeConverter
    fun fromVerificationStatus(value: VerificationStatus?): String? = value?.name
    @TypeConverter
    fun toVerificationStatus(value: String?): VerificationStatus? = value?.let { enumValueOf<VerificationStatus>(it) }

    @TypeConverter
    fun fromMealPeriod(value: MealPeriod?): String? = value?.name
    @TypeConverter
    fun toMealPeriod(value: String?): MealPeriod? = value?.let { enumValueOf<MealPeriod>(it) }

    @TypeConverter
    fun fromDonationStatus(value: DonationStatus?): String? = value?.name
    @TypeConverter
    fun toDonationStatus(value: String?): DonationStatus? = value?.let { enumValueOf<DonationStatus>(it) }

    @TypeConverter
    fun fromRequestStatus(value: RequestStatus?): String? = value?.name
    @TypeConverter
    fun toRequestStatus(value: String?): RequestStatus? = value?.let { enumValueOf<RequestStatus>(it) }

    @TypeConverter
    fun fromTransportPreference(value: TransportPreference?): String? = value?.name
    @TypeConverter
    fun toTransportPreference(value: String?): TransportPreference? = value?.let {
        try { enumValueOf<TransportPreference>(it) } catch (_: Exception) { TransportPreference.UBER_RAPIDO_BOOKING }
    }

    @TypeConverter
    fun fromNotificationType(value: NotificationType?): String? = value?.name
    @TypeConverter
    fun toNotificationType(value: String?): NotificationType? = value?.let { enumValueOf<NotificationType>(it) }
}
