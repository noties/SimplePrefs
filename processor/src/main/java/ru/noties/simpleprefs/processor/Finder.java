package ru.noties.simpleprefs.processor;

import java.util.List;

import javax.lang.model.element.Element;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public interface Finder {
    String findName(String keyName, Element key, List<? extends Element> enclosed);
    TypeChecker getChecker();
    String getFindingMethodName();
}
