package xyz.nabijaczleweli.gen_epub_book

import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.regex.Pattern

import scala.io.{Codec, Source}

object Util {
	private lazy val mimeTypeMap =
		Assets.mimeTypes.lines filterNot (_ startsWith "#") map (_ split " ") map (kvs => ((kvs.iterator drop 1).toSeq, kvs(0))) flatMap (kvs =>
			kvs._1 map ((_, kvs._2))) toMap
	private val titleRgx = Pattern.compile("""<!-- ePub title: "([^"]+)" -->""")
	val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

	def pathID(of: File): String = pathID(of.getPath)
	def pathID(of: String): String =
		pathFilename(of).replace('.', '_')

	def pathFilename(of: File): String = pathFilename(of.getPath)
	def pathFilename(of: String): String =
		of.replace('\\', '/').replace("../", "").replace("./", "").replace('/', '-')

	def urlId(of: URL): String =
		urlFilename(of).replace('.', '_')

	def urlFilename(of: URL): String =
		of.getFile.substring(of.getFile.lastIndexOf('/') + 1)

	def findTitle(in: File): Option[String] =
		Source.fromFile(in)(Codec.UTF8).getLines map titleRgx.matcher find (_.matches) map (_ group 1)

	def guessMimeType(fname: String): String =
		fname lastIndexOf '.' match {
			case -1 => "text/plain"
			case idx => mimeTypeMap.getOrElse(fname substring (idx + 1), "text/plain")
		}
}
