package xyz.nabijaczleweli.gen_epub_book.feature

import java.io.File

import xyz.nabijaczleweli.gen_epub_book.Util

class IncludeDirectory(val name: Option[String], val dir: File) {
	def ebookPath(fname: String): String =
		name match {
			case Some(n) => s"$n/${Util pathFilename fname}"
			case None => Util pathFilename fname
		}

	def ebookID(fname: String): String =
		name match {
			case Some(n) => s"$n/${Util pathID fname}"
			case None => Util pathID fname
		}

	def resolve(fname: String): File =
		new File(dir, fname)


	override def toString: String =
		name match {
			case Some(n) => s"""$n="$dir""""
			case None => s""""$dir""""
		}

	override def equals(obj: Any): Boolean =
		obj != null && obj.isInstanceOf[IncludeDirectory] && (obj.asInstanceOf[IncludeDirectory].dir equals dir) &&
			(obj.asInstanceOf[IncludeDirectory].name equals name)
}

object IncludeDirectory {
	def find(where: List[IncludeDirectory], whom: String): Option[IncludeDirectory] =
		where map (i => (i, i resolve whom)) find (f => f._2.exists && f._2.isFile) map (_._1)
}
