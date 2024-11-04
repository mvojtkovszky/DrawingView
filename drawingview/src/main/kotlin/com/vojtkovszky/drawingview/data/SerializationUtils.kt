package com.vojtkovszky.drawingview.data

import com.vojtkovszky.drawingview.data.path.PathAddCircle
import com.vojtkovszky.drawingview.data.path.PathInfo
import com.vojtkovszky.drawingview.data.path.PathMoveTo
import com.vojtkovszky.drawingview.data.path.PathQuadTo
import com.vojtkovszky.drawingview.data.path.PathReset
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

internal class SerializationUtils {
    companion object {
        val module = SerializersModule {
            polymorphic(PathInfo::class, PathAddCircle::class, PathAddCircle.serializer())
            polymorphic(PathInfo::class, PathMoveTo::class, PathMoveTo.serializer())
            polymorphic(PathInfo::class, PathQuadTo::class, PathQuadTo.serializer())
            polymorphic(PathInfo::class, PathReset::class, PathReset.serializer())
        }

        val json = Json {
            serializersModule = module
            classDiscriminator = "type"
        }
    }
}