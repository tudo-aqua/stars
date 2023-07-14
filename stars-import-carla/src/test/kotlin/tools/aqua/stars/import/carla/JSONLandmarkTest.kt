package tools.aqua.stars.import.carla

import tools.aqua.stars.data.av.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JSONLandmarkTest {

    private lateinit var completeJsonLandmark: JsonLandmark
    private lateinit var completeLandmark: Landmark
    private lateinit var jsonSpeedLimit: JsonLandmark
    private lateinit var speedLimit: Landmark

    @BeforeTest
    fun setup() {
        completeJsonLandmark = JsonLandmark(
            id = 100,
            roadId = 1,
            location = JsonLocation(0.0, 0.0, 0.0),
            text = "Landmark",
            unit = "mph",
            value = 30.0,
            type = JsonLandmarkType.MaximumSpeed,
            country = "Test",
            distance = 10.0,
            rotation = JsonRotation(0.0, 0.0, 0.0),
            hOffset = 0.0,
            height = 2.3,
            isDynamic = false,
            name = "SpeedLimit",
            orientation = JsonLandmarkOrientation.Both,
            pitch = 0.0,
            roll = 0.0,
            s = 10.0,
            subType = "Subtype",
            width = 1.0,
            zOffset = 0.0
        )
        completeLandmark = completeJsonLandmark.toLandmark()
    }

    @Test
    fun checkAttributeSetter() {
        assertEquals(completeJsonLandmark.id, completeLandmark.id)
        assertEquals(completeJsonLandmark.country, completeLandmark.country)
        assertEquals(completeJsonLandmark.distance, completeLandmark.distance)
        assertEquals(completeJsonLandmark.s, completeLandmark.s)
        assertEquals(completeJsonLandmark.location.x, completeLandmark.location.x)
        assertEquals(completeJsonLandmark.location.y, completeLandmark.location.y)
        assertEquals(completeJsonLandmark.location.z, completeLandmark.location.z)
        assertEquals(completeJsonLandmark.rotation.yaw, completeLandmark.rotation.yaw)
        assertEquals(completeJsonLandmark.rotation.pitch, completeLandmark.rotation.pitch)
        assertEquals(completeJsonLandmark.rotation.roll, completeLandmark.rotation.roll)
        assertEquals(completeJsonLandmark.name, completeLandmark.name)
        assertEquals(completeJsonLandmark.text, completeLandmark.text)
        assertEquals(completeJsonLandmark.type.value, completeLandmark.type.value)
        assertEquals(completeJsonLandmark.value, completeLandmark.value)
        assertEquals(completeJsonLandmark.unit, completeLandmark.unit)
    }
}