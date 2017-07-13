/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.logic.buildings.military.occupying;

import static java8.util.stream.StreamSupport.stream;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jsettlers.common.buildings.OccupierPlace;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.ESoldierType;

import java8.util.stream.Collectors;

/**
 * Created by Andreas Eberle on 03.07.2017.
 */
class SoldierRequests implements Serializable {
	private final LinkedList<SoldierRequest>[] requestsByClass;

	SoldierRequests() {
		// noinspection unchecked
		this.requestsByClass = new LinkedList[] { new LinkedList<SoldierRequest>(), new LinkedList<SoldierRequest>() };
	}

	SoldierRequest removeOne(ESoldierType soldierType) {
		LinkedList<SoldierRequest> classRequests = requestsByClass[soldierType.soldierClass.ordinal];

		if (classRequests.isEmpty()) {
			return null;
		}

		for (Iterator<SoldierRequest> iterator = classRequests.iterator(); iterator.hasNext();) { // check if there is a request with this soldier type
			SoldierRequest request = iterator.next();
			if (request.soldierType == soldierType) {
				iterator.remove();
				return request;
			}
		}

		return classRequests.removeFirst();
	}

	int getCount(ESoldierClass soldierClass) {
		return requestsByClass[soldierClass.ordinal].size();
	}

	void clear() {
		requestsByClass[0].clear();
		requestsByClass[1].clear();
	}

	boolean isEmpty() {
		return requestsByClass[0].size() + requestsByClass[1].size() <= 0;
	}

	Set<ESearchType> getRequestedSearchTypes() {
		Set<ESearchType> requestedSearchTypes = EnumSet.noneOf(ESearchType.class);
		for (LinkedList<SoldierRequest> classRequests : requestsByClass) {
			requestedSearchTypes.addAll(stream(classRequests).map(SoldierRequest::getSearchType).collect(Collectors.toList()));
		}
		return requestedSearchTypes;
	}

	void add(SoldierRequest soldierRequest) {
		ESoldierClass soldierClass = soldierRequest.soldierType != null ? soldierRequest.soldierType.soldierClass : soldierRequest.soldierClass;
		requestsByClass[soldierClass.ordinal].add(soldierRequest);
	}

	List<OccupierPlace> getPlaces() {
		List<OccupierPlace> places = new LinkedList<>();
		places.addAll(stream(requestsByClass[0]).map(request -> request.place).collect(Collectors.toList()));
		places.addAll(stream(requestsByClass[0]).map(request -> request.place).collect(Collectors.toList()));
		return places;
	}
}
