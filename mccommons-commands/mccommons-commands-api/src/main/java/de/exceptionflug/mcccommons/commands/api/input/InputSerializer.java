package de.exceptionflug.mcccommons.commands.api.input;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class InputSerializer {
	public final List<InputSerializable> serializables = new ArrayList<>();


	/**
	 * Register a serializable to our list
	 *
	 * @param lightningSerializable Serializable to register
	 */
	public void registerSerializable(@NonNull final InputSerializable<?> lightningSerializable) {
		Preconditions.checkNotNull(lightningSerializable.getClazz(), "Class of serializable mustn't be null");
		serializables.add(lightningSerializable);
	}

	public InputSerializable<?> findSerializable(final Class<?> clazz) {
		for (final InputSerializable<?> serializable : serializables) {
			if (serializable.getClazz().equals(clazz)) {
				return serializable;
			}
		}
		return null;
	}

	@SuppressWarnings("ALL")
	public <T> T serialize(final Class<T> clazz, final String input) {
		final InputSerializable serializable = findSerializable(clazz);
		Preconditions.checkNotNull(serializable, "No serializable found for '" + clazz.getSimpleName() + "'");
		return (T) serializable.serialize(input);
	}
}
