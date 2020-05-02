#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location=0) in vec2 vertex;
layout(location=1) in vec2 texcoord;
layout(location=2) in vec2 co_vertex;

layout(location=3) in vec4 color_in;
layout(location=4) in vec4 trans_in;

layout(constant_id=1) const int MAX_GLOBALTRANS_COUNT = 4;

layout(binding=0) uniform GlobalData {
    mat4 projection;
    mat4 globalTrans[MAX_GLOBALTRANS_COUNT];
} global;


layout(push_constant) uniform UnifiedPerCall {
    int globalTransIndex;
    int texIndex;
} local;

layout (location=0) out vec2 frag_texcoord;
layout (location=1) flat out int frag_mode;
layout (location=2) out float frag_intensity;
layout (location=3) out vec4 frag_color;

void main() {
    float int_mode = trans_in.w/10.0;
    int mode = int(floor(int_mode));
    frag_intensity = ((int_mode-float(mode))*10.0)-1.0;
    frag_color = color_in;
    frag_mode = mode;

    vec2 local_vert;
    if(mode != 0) {
        frag_texcoord = texcoord;
        local_vert = vertex;
    } else {
        frag_texcoord = vec2(0, 0);
        local_vert = co_vertex;
    }

    vec4 transformed = vec4(local_vert, 0, 1);
    transformed.xyz += trans_in.xyz;

    gl_Position = global.projection * global.globalTrans[local.globalTransIndex] * transformed;
}