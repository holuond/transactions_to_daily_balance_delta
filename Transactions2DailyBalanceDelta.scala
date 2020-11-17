import java.io.FileWriter
import java.io.File
import java.io.BufferedWriter
import java.text.SimpleDateFormat
import java.util.{Calendar, GregorianCalendar}
import scala.collection.mutable.ListBuffer

/**
 *  This script takes an input CSV file filled with transactions (date & amount)
 *  and outputs a CSV file containing total balance change per day.
 *
 *  Example input:   -->    Example output:
 *  29.01.2020,300          2020-01-29,200
 *  29.01.2020,-100         2020-01-30,0
 *  31.01.2020,99           2020-01-31,99
 *
 */
object Transactions2DailyBalanceDelta {
  def main(args: Array[String]): Unit = {

    // Customizable Input Params
    val inputFile = "_"
    val outputFile = "_"
    val inputDateFormat = "dd.MM.yyyy"

    val bufferedSource = scala.io.Source.fromFile(inputFile)
    
    val inputDateFormatter = new SimpleDateFormat(inputDateFormat)
    val outputDateFormatter = new SimpleDateFormat("yyyy-MM-dd")

    // List of transactions as tuples:
    // (date in output format, amount)
    val transactions: List[(String, Double)] = bufferedSource
      .getLines
      .map(_.split(","))
      .map(x => (outputDateFormatter.format(inputDateFormatter.parse(x(0))), x(1).toDouble))
      .toList

    // Map of days on which at least one transaction happened as tuples:
    // (day in output format, total balance change on given day)
    val activeDaysDeltas: Map[String, Double] = transactions
      .foldLeft(scala.collection.mutable.Map[String, Double]()) {
        case (map, (date, amount)) =>
          map(date) = map.getOrElse(date, 0.0) + amount
          map
      }
      .toMap

    val completeDateRange = getDateRange(activeDaysDeltas.keys.min, activeDaysDeltas.keys.max, outputDateFormatter)
    // List of all days between min and max days found in input and balance delta on that day as tuples:
    // (day in output format, total balance change on given day)
    val dailyDeltas: Seq[(String, Double)] = completeDateRange.map(date => (date, activeDaysDeltas.getOrElse(date, 0.0)))

    println("Transactions: " + transactions)
    println("Total change of balance on given day: " + dailyDeltas)

    writeLinesIntoFile(outputFile, dailyDeltas.toList.map(x => x._1 + "," + x._2.toString))
  }

  private def writeLinesIntoFile(pathname: String, lines: List[String]): Unit = {
    val bw = new BufferedWriter(new FileWriter(new File(pathname)))
    for (line <- lines) {
      bw.write(line + "\n")
    }
    bw.close()
  }
  
  private def getDateRange(startDate: String, endDate: String, formatter: SimpleDateFormat): List[String] = {
    val dates: scala.collection.mutable.ListBuffer[String] = ListBuffer()
    val calendar = new GregorianCalendar()
    calendar.setTime(formatter.parse(startDate))
    while (calendar.getTime.before(formatter.parse(endDate)) || formatter.parse(formatter.format(calendar.getTime)).equals(formatter.parse(endDate))) {
      dates += (formatter.format(calendar.getTime))
      calendar.add(Calendar.DATE, 1)
    }
    dates.toList
  }
}
