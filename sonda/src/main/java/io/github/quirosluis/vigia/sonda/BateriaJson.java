package io.github.quirosluis.vigia.sonda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BateriaJson(String plugged, Integer percentage) {}
