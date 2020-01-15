#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location=0) in vec3 position;
layout(location=1) in vec2 scale;
layout(location=2) in vec4 color;
layout(location=3) in vec3 additional; // intensity,index,mode

layout(constant_id=1) const int MAX_GLOBALTRANS_COUNT = 4;

layout(binding=0) uniform GlobalData {
    mat4 projection;
    mat4 globalTrans[MAX_GLOBALTRANS_COUNT];
} global;

layout(constant_id=2) const int MAX_GEOMETRY_BUFFER_COUNT = 10;

layout(binding=3) uniform GeometryData {
    vec4 geometryData[4*1000];
} geomtryBuffer[MAX_GEOMETRY_BUFFER_COUNT];

layout(push_constant) uniform UnifiedPerCall {
    int globalTransIndex;
    int texIndex;
    int geometryIndex;
} local;

layout (location=0) out vec2 frag_texcoord;
layout (location=1) flat out int frag_mode;
layout (location=2) out float frag_intensity;
layout (location=3) out vec4 frag_color;

void main() {
    int mode = int(additional.z);
    frag_mode = mode;
    frag_intensity = additional.x;
    frag_color = color;

    int index = gl_VertexIndex+(int(additional.y));

    frag_texcoord = geomtryBuffer[local.geometryIndex].geometryData[index].zw;
    vec2 local_vert = geomtryBuffer[local.geometryIndex].geometryData[index].xy;

    vec4 transformed = vec4(local_vert*scale, 0, 1);
    transformed.xyz += position;

    //if(gl_VertexIndex == 1 || gl_VertexIndex == 2) transformed.x += 10;
    //if(gl_VertexIndex == 2 || gl_VertexIndex == 3) transformed.y += 10;

    gl_Position = global.projection * global.globalTrans[local.globalTransIndex] * transformed;
}