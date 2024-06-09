package org.tensorflow.lite.examples.poseestimation.data

import android.graphics.PointF
import kotlin.math.pow
import kotlin.math.sqrt

class KeyPointComparer {
    fun areListPersonsSimilar(persons1: List<Person>, persons2: List<Person>, threshold: Float): Boolean {
        if (persons1.size != persons2.size) {
            return false
        }

        for (i in persons1.indices) {
            val person1 = persons1[i]
            val person2 = persons2[i]

            if (!arePersonsSimilar(person1, person2, threshold)) {
                return false
            }
        }
        return true
    }
    fun arePersonsSimilar(person1: Person, person2: Person, threshold: Float): Boolean {
        if (person1.keyPoints.size != person2.keyPoints.size) {
            return false
        }

        for (i in person1.keyPoints.indices) {
            val kp1 = person1.keyPoints[i]
            val kp2 = person2.keyPoints[i]

            if (kp1.bodyPart != kp2.bodyPart) {
                return false
            }

            if (kp1.score > 0.3) {
                val distance = euclideanDistance(kp1.coordinate, kp2.coordinate)
                if (distance > threshold) {
                    return false
                }
            }
        }
        return true
    }

    fun euclideanDistance(point1: PointF, point2: PointF): Float {
        return sqrt((point1.x - point2.x).pow(2) + (point1.y - point2.y).pow(2))
    }

}