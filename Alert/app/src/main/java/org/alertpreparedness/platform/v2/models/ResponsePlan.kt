package org.alertpreparedness.platform.v2.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.alertpreparedness.platform.v2.models.enums.ResponsePlanState
import org.alertpreparedness.platform.v2.models.enums.ResponsePlanState.NOT_APPROVED
import org.alertpreparedness.platform.v2.models.enums.ResponsePlanStateSerializer
import java.lang.reflect.Type

class ResponsePlan(
        val approval: ResponsePlanApproval,
        val name: String,
        val status: ResponsePlanState
): BaseModel()

class ResponsePlanApproval(
        val countryDirector: ResponsePlanApprovalState?
)

class ResponsePlanApprovalState(
        val id: String,
        val state: ResponsePlanState
)

object ResponsePlanApprovalSerializer: JsonDeserializer<ResponsePlanApproval> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?,
            context: JsonDeserializationContext?): ResponsePlanApproval {
        val countryDirector = json?.asJsonObject?.get("countryDirector")?.asJsonObject
        val countryId = countryDirector?.keySet()?.first()
        val countryState = countryDirector?.get(countryId)?.asInt

        return ResponsePlanApproval(
                if(countryId != null) ResponsePlanApprovalState(countryId, ResponsePlanStateSerializer.jsonToEnum(countryState!!) ?: NOT_APPROVED) else null
        )
    }
}