package com.github.jarod.exocet.cache;

public interface ValueLoader<T> {

	void load(Result<T> res) throws Exception;

}
