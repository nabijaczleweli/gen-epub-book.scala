package xyz.nabijaczleweli.gen_epub_book

import java.io.File

class Settings {
	var inFile: File = _
	var outFile: File = _
	var relativeDirectoryRoot: String = _

	def this(args: Array[String]) {
		this()

		assert(!(args.length < 2), "Not enough arguments, required: infile, outfile")
		assert(!(args.length > 2), "Too many arguments, required: infile, outfile")

		inFile = new File(args(0))
		outFile = new File(args(1))
		relativeDirectoryRoot = inFile.getParent + '/'

		if(!inFile.exists || !inFile.isFile)
			throw new RuntimeException(s"Input file ${args(0)} nonexistant")
	}
}
