package xyz.nabijaczleweli.gen_epub_book

import java.io.File
import java.text.ParseException

import xyz.nabijaczleweli.gen_epub_book.feature.IncludeDirectory

class Settings(private val args: Array[String]) {
	var inFile: File = _
	var outFile: File = _
	var includeDirs: List[IncludeDirectory] = Nil
	var separator: String = ":"

	for(arg <- args)
		arg match {
			case ("-h" | "--help") =>
				printAndQuit()
			case _ =>
				Settings isOpt arg match {
					case 0 =>
						if(inFile == null)
							inFile = new File(arg)
						else if(outFile == null)
							outFile = new File(arg)
						else
							printAndQuit()
					case 1 =>
						Settings getSingleOpt arg match {
							case ('I', incdir) =>
								incdir.split("=", 2) match {
									case Array(path) =>
										includeDirs :+= new IncludeDirectory(None, new File(path))
									case Array("", _) =>
										printAndQuit()
									case Array(name, path) =>
										includeDirs :+= new IncludeDirectory(Some(name), new File(path))
									case _ =>
										printAndQuit()
								}
							case ('S', sep) =>
								separator = sep
							case (_, _) =>
								printAndQuit()
						}
					case 2 =>
						printAndQuit()
				}
		}

	if(inFile == null || outFile == null)
		printAndQuit()

	if(!inFile.exists || !inFile.isFile)
		throw new ParseException(s"Input file ${args(0)} nonexistant", 0)

	for(i <- includeDirs)
		if(!i.dir.exists || !i.dir.isDirectory)
			throw new ParseException(s"Include directory ${i.dir} nonexistant", 0)

	includeDirs ::= new IncludeDirectory(None, new File(inFile.getParent))


	private lazy val usage =
		s"""USAGE:
			|   gen-epub-book [-I<include directory>]... [-S<separator>] [-h] <input file> <output file>
			|
			|Where:
			|
			|   -I[name=]path
			|     Add an include directory
			|
			|   -Sseparator
			|     Override separator. Default: ":"
			|
			|   -h, --help
			|     Displays usage information and exits.
			|
			|   <input file>
			|     (required) File to parse, must exist
			|
			|   <output file>
			|     (required) File to write the book to
			|
			|gen-epub-book.scala -- generate an ePub book from a simple plaintext descriptor
			|                       https://nabijaczleweli.xyz/content/gen-epub-book
		""".stripMargin

	private def printAndQuit(): Unit = {
		println(usage)
		System exit 0
	}
}

private object Settings {
	def isOpt(who: String): Int =
		if(who.startsWith("--"))
			2
		else if(who.startsWith("-"))
			1
		else
			0

	def getSingleOpt(who: String): (Char, String) =
		(who(1), (who substring 2).trim)
}
