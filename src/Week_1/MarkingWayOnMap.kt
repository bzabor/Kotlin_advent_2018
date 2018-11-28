package Week_1

var spotArray: Array<Array<Spot>>? = null
var startPos: Pair<Int,Int>? = null
var endPos: Pair<Int,Int>? = null


enum class SpotType {
    PLAIN, START, END, OBSTACLE
}

// Each SPOT on the grid knows
//      its position on the grid
//      its TYPE (START, END, PLAIN
//      its minimum accumulated distance from the START

data class Spot(
    var x: Int = 0,
    var y: Int = 0,
    var spotType: SpotType = SpotType.PLAIN,
    var accumulatedDistance: Double = 0.0

) {
    var path: MutableList<Spot> = mutableListOf()
    override fun toString() = "$x:$y"
}


fun addPath(path: String = ""): String {
    val rows = path.split("\n")
    val spots = Array(rows.size) { Array(rows[0].length) { Spot() } }
    for ((rowIdx, row) in rows.iterator().withIndex()) {
        for ((colIdx, value) in row.iterator().withIndex()) {
            spots[rowIdx][colIdx].x = rowIdx
            spots[rowIdx][colIdx].y = colIdx
            spots[rowIdx][colIdx].spotType = when (value) {
                'S' -> {
                    startPos = Pair(rowIdx, colIdx)
                    SpotType.START
                }
                'X' -> {
                    endPos = Pair(rowIdx, colIdx)
                    SpotType.END
                }
                'B' -> SpotType.OBSTACLE
                else -> SpotType.PLAIN
            }
        }
    }
    spotArray = spots

    printArray(spotArray)
    step(spotArray!![startPos!!.first][startPos!!.second])
    printArray(spotArray, true)
    return buildResultString()
}

fun step(spot: Spot) {
    val neighbors = mutableListOf<Spot>()
    for (i in spot.x - 1..spot.x + 1) {
        for (j in spot.y - 1..spot.y + 1) {
            if (inBounds(i, j) && !(i == spot.x && j == spot.y)) {
                neighbors.add(spotArray!![i][j])
            }
        }
    }

    for (neighbor in neighbors) {
        val stepSize = stepSize(spot, neighbor)
        if (neighbor.spotType != SpotType.START && neighbor.spotType != SpotType.OBSTACLE &&
            (neighbor.accumulatedDistance == 0.0 ||
                    (!spot.path.contains(neighbor) && spot.accumulatedDistance + stepSize < neighbor.accumulatedDistance))) {

            neighbor.accumulatedDistance = spot.accumulatedDistance + stepSize
            neighbor.path = spot.path.toMutableList()
            neighbor.path.add(spot)

            if (neighbor.spotType != SpotType.END) {
                step(neighbor)
            }
        }
    }
}

fun stepSize(spot1: Spot, spot2: Spot): Double =
    if (spot1.x == spot2.x || spot1.y == spot2.y) 1.0 else 1.5


fun inBounds(x: Int, y: Int): Boolean {
    return x in 0..spotArray!!.lastIndex && y in 0..spotArray!![0].lastIndex
}

fun printArray(arr: Array<Array<Spot>>?, showSolution: Boolean = false) {
    println("")
    println(if (showSolution) "SOLUTION:" else "BEFORE:")
    for (row in arr!!) {
        for (item in row) {
            print(
                when (item.spotType) {
                    SpotType.START -> "S "
                    SpotType.OBSTACLE -> "B "
                    SpotType.PLAIN ->
                        if (showSolution  && spotArray!![endPos!!.first][endPos!!.second].path.contains(item)) "* " else  ". "
                    SpotType.END -> "X "
                }
            )
        }
        println("")
    }
    if (showSolution) println ("Shortest Path Distance: ${spotArray!![endPos!!.first][endPos!!.second].accumulatedDistance}")
}

fun buildResultString(): String {
    var result = ""
    for (row in spotArray!!) {
        for (spot in row) {
            when (spot.spotType) {
                SpotType.START -> result +="*"
                SpotType.OBSTACLE -> result +="B"
                SpotType.PLAIN ->
                    if (spotArray!![endPos!!.first][endPos!!.second].path.contains(spot)) result +="*" else result +="."
                SpotType.END -> result +="*"
            }
        }
        result +="\n"
    }

    println("\n\nWeek_1.buildResultString result:\n $result")
    return result.trim()
}

//val mapString = """
//        ....................
//        ......X...B.........
//        ..........B.........
//        ........BBB....S....
//        ....................
//        """.trimIndent()

//fun main(args: Array<String>) {
//    Week_1.addPath(mapString)
//    Week_1.printArray(Week_1.getSpotArray)
//    Week_1.step(Week_1.getSpotArray!![Week_1.getStartPos!!.first][Week_1.getStartPos!!.second])
//    Week_1.printArray(Week_1.getSpotArray, true)
//}

//fun readablePath(spot: Week_1.Spot): String {
//    var readable = ""
//    for (pathSpot in spot.path) {
//        readable = readable.plus("${pathSpot} --> ")
//    }
//    return readable + spot
//}
