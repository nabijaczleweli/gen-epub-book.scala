package xyz.nabijaczleweli.gen_epub_book

import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.regex.Pattern

import scala.io.Source

object Util {
	private val titleRgx = Pattern.compile("""<!-- ePub title: "([^"]+)" -->""")
	val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

	def pathId(of: String): String = {
		val f = pathFilename(of)
		f.lastIndexOf('.') match {
			case -1 => f
			case i => f.substring(0, i)
		}
	}

	def pathFilename(of: String): String =
		of.replace('\\', '/').replace("../", "").replace("./", "").replace('/', '-')

	def urlId(of: URL): String =
		pathId(urlFilename(of))

	def urlFilename(of: URL): String =
		of.getFile.substring(of.getFile.lastIndexOf('/') + 1)

	def findTitle(in: File): Option[String] =
		(Source fromFile in).getLines map titleRgx.matcher find (_.matches) map (_ group 1)
}
