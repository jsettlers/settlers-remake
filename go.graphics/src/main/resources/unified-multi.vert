#version 330 core

precision mediump float;

in vec3 position; //attribute
in vec2 scale; //attribute
in vec4 color; //attribute
in vec3 additional; //attribute

out vec2 geom_scale;
out vec4 geom_color;
out float geom_intensity;
out float geom_index;
out float geom_mode;

void main() {
	gl_Position = vec4(position, 1);
	geom_scale = scale;

	geom_color = color;
	geom_mode = additional.z;
	geom_index = additional.y;
	geom_intensity = additional.x;
}
