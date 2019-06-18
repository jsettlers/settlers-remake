#version 310 es

precision mediump float;

in vec3 position; //attribute
in vec4 color; //attribute
in float index; //attribute
in float intensity; //attribute

out vec4 geom_color;
out float geom_index;
out float geom_intensity;

void main() {
    gl_Position = vec4(position, 1);

    geom_color = color;
    geom_index = (index);
    geom_intensity = intensity;
}
