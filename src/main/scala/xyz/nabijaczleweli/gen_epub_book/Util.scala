package xyz.nabijaczleweli.gen_epub_book

import java.net.URL

object Util {
	def pathId(of: String) = {
		val f = pathFilename(of)
		f.lastIndexOf('.') match {
			case -1 => f
			case i => f.substring(0, i)
		}
	}

	def pathFilename(of: String) =
		of.replace('\\', '/').replace("../", "").replace("./", "").replace('/', '-')

	def urlId(of: URL) =
		pathId(urlFilename(of))

	def urlFilename(of: URL) =
		of.getFile.substring(of.getFile.lastIndexOf('/') + 1)
}
