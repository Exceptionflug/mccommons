package de.exceptionflug.mccommons.core;

public interface Converter<Source, Target> {

    Target convert(Source src);

}
