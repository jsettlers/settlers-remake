#version 300 es

precision mediump float;

in vec2 vertex; //attribute
in vec2 texcoord; //attribute

uniform mat4 globalTransform;
uniform vec4 transform[100];
uniform vec4 color[100];
uniform mat4 projection;

uniform vec2 uni_info; // x=image, y=shadow

out vec2 frag_texcoord;
out vec4 frag_color;
out float frag_intensity;

void main() {
    vec4 transformed = vec4(vertex, 0, 1);
    transformed.xyz = transformed.xyz+transform[gl_InstanceID].xyz;
    gl_Position = projection * globalTransform * transformed;
    frag_texcoord = texcoord;

    frag_color = color[gl_InstanceID];
    frag_intensity = transform[gl_InstanceID].w;
}
