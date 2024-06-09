package org.tensorflow.lite.examples.poseestimation


class GameItem(
    var id: Int,
    var category: String,
    var heading: String,
    var level: String,
    var time: String,
    var imageURL: String
) {
    //var baseImgResults: List<KeyPoint> = ArrayList<KeyPoint>()

    override fun toString(): String {
        return "GameItem{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", heading='" + heading + '\'' +
                ", level='" + level + '\'' +
                ", time='" + time + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}'
    }

//    fun setBaseImgResults(list: List<KeyPoint>) {
//        this.baseImgResults = list
//    }
//
//    fun getBaseImgResults(): List<KeyPoint> {
//        return this.baseImgResults
//    }
}
