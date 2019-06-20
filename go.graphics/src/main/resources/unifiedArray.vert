#version 100

#extension GL_EXT_draw_instanced : enable

precision mediump float;

attribute vec2 vertex;
attribute vec2 texcoord;

uniform mat4 globalTransform;
uniform vec4 transform[100];
uniform vec4 color[100];
uniform mat4 projection;

uniform vec2 uni_info; // x=image, y=shadow

varying vec2 frag_texcoord;
varying vec4 frag_color;
varying float frag_intensity;

void main() {
    vec4 transformed = vec4(vertex, 0, 1);
    transformed.xyz = transformed.xyz+transform[gl_InstanceID].xyz;
    gl_Position = projection * globalTransform * transformed;
    frag_texcoord = texcoord;

    frag_color = color[gl_InstanceID];
    frag_intensity = transform[gl_InstanceID].w;
}
