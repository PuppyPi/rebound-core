package rebound.text.encodings.detection.detectors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.NullaryFunctionThrowingIOException;

/**
 * This includes the Python conventions :>
 * 		https://www.python.org/dev/peps/pep-0263/
 */
public enum StandardTextEditorConventionsTextEncodingDetector
implements TextEncodingDetector
{
	I;
	
	
	
	@Override
	public Charset detectEncoding(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException, UnsupportedCharsetException
	{
		//TODO DO IT :D
		
		/*
		 * First or second line (\n, \r, or \r\n):
		 * We can just remove \0's from the stream to account for UCS2 and UCS4 :>
		 * 		
		 * 		"# coding=<encoding name>"
		 * 		"# -*- coding: <encoding name> -*-"
		 * 		"# vim: set fileencoding=<encoding name> :"
		 * 
		 * 		^[ \t\f]*#.*?coding[:=][ \t]*([-_.a-zA-Z0-9]+)
		 */
		
		return null;
	}
	
	
	
	
	
	
	/* For test data:
	 * 
			# -*- coding: latin-1 -*-
			# -*- coding: iso-8859-15 -*-
			# -*- coding: ascii -*-
			# This Python file uses the following encoding: utf-8
			
			#!/usr/local/bin/python
			# coding: latin-1
	 */
}
