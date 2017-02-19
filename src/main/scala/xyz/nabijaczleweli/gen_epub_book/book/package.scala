package xyz.nabijaczleweli.gen_epub_book

import java.io.File

import scala.io.Source

package object book {
	def load(from: File): List[Element] =
		((Source fromFile from).getLines flatMap Element.parseLine).toList
}
