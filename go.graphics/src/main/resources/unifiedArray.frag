#version 300 es

#extension GL_NV_fragdepth : enable

precision mediump float;

in vec2 frag_texcoord;
in vec4 frag_color;
in float frag_intensity;

uniform sampler2D texHandle;
uniform float shadow_depth;
uniform vec2 uni_info; // x=image, y=shadow

out vec4 out_color;
out float gl_FragDepth;

void main() {
    vec4 tex_color = texture(texHandle, frag_texcoord);
    out_color = vec4(0,0,0,0);

    if(uni_info.x > 0.1) { // draw image
        if(tex_color.a < 0.1 && tex_color.r > 0.1) { // torso pixel
            out_color.rgb = frag_color.rgb*tex_color.b;
            out_color.a = frag_color.a;
        } else {
            out_color = tex_color;
        }
    }
    float fragDepth = gl_FragCoord.z;

    if(uni_info.y > 0.1 && tex_color.g > 0.1 && tex_color.a < 0.1) { // shadow pixel
        out_color.rgba = tex_color.aaag;
        fragDepth += shadow_depth;
    }

    out_color.rgb *= frag_intensity;

    if(out_color.a < 0.5) discard;

    gl_FragDepth = fragDepth;
}
