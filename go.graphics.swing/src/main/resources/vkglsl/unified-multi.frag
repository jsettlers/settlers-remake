#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(constant_id=0) const int MAX_TEXTURE_COUNT = 1;
layout (binding=1) uniform sampler2D texHandle[MAX_TEXTURE_COUNT];

layout(binding=2) uniform UnifiedData {
    float shadow_depth;
} unified;

layout(push_constant) uniform UnifiedPerCall {
    int globalTransIndex;
    int texIndex;
    int geometryIndex;
} local;

layout (location=0) in vec2 frag_texcoord;
layout (location=1) flat in int frag_mode;
layout (location=2) in float frag_intensity;
layout (location=3) in vec4 frag_color;

layout (location=0) out vec4 out_color;

void main() {
    float fragDepth = gl_FragCoord.z;
    vec4 fragColor = frag_color;

    bool textured = frag_mode!=0;

    if(textured) {
        bool progress_fence = frag_mode > 3;

        vec4 tex_color;
        if(progress_fence) {
            tex_color = texture(texHandle[local.texIndex], fragColor.rg+(fragColor.ba-fragColor.rg)*frag_texcoord);
        } else {
            tex_color = texture(texHandle[local.texIndex], frag_texcoord);
        }

        bool image_fence = frag_mode>0;
        bool torso_fence = frag_mode>1 && !progress_fence;
        bool shadow_fence = abs(float(frag_mode))>2.0 && !progress_fence;

        if(torso_fence && tex_color.a < 0.1 && tex_color.r > 0.1) { // torso pixel
            fragColor.rgb *= tex_color.b;
        } else if(shadow_fence && tex_color.a < 0.1 && tex_color.g > 0.1) { // shadow pixel
            fragColor.rgba = tex_color.aaag;
            fragDepth += unified.shadow_depth;
        } else if(image_fence) { // image pixel
            if(!torso_fence && !shadow_fence && !progress_fence) {
                fragColor *= tex_color;
            } else {
                fragColor = tex_color;
            }
        }
    }

    if(fragColor.a < 0.5) discard;

    fragColor.rgb *= frag_intensity;

    out_color = fragColor;
    gl_FragDepth = fragDepth;
}