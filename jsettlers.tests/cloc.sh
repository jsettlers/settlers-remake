#!/bin/sh

# Count the line of settlers code we wrote.

./cloc.pl \
	../go.graphics.android/src \
	../go.graphics.nativegl/src \
	../go.graphics/src \
	../go.graphics.swing/src \
	../jsettlers.algorithms/src \
	../jsettlers.buildingcreator/src \
	../jsettlers.common/src \
	../jsettlers.common/resources \
	../jsettlers.graphics.androidui/src \
	../jsettlers.graphics.androidui/res \
	../jsettlers.graphics/src \
	../jsettlers.graphics.swing/src \
	../jsettlers.logic/jsettlers* \
	../jsettlers.main.android/src \
	../jsettlers.main.android/res \
	../jsettlers.main.swing/src \
	../jsettlers.mapcreator/src \
	../jsettlers.network/jsettlers* \
	../jsettlers.networking/src \
	../jsettlers.networking/test \
	../jsettlers.network.objects/jsettlers* \
	../jsettlers.tests/src \
	--force-lang="Bourne Shell",txt \
	--force-lang="Bourne Shell",properties \
        --force-lang="Bourne Shell",props 
	

