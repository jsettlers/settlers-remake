#version 100

#extension GL_NV_fragdepth : enable

precision mediump float;

varying vec2 frag_texcoord;

uniform sampler2D texHandle;
uniform float shadow_depth;
uniform vec3 uni_info; // x=image, y=shadow, z=intensity
uniform vec4 color;

void main() {
	vec4 tex_color = texture2D(texHandle, frag_texcoord);
	gl_FragColor = vec4(0,0,0,0);

	if(uni_info.x > 0.1) { // draw image
		if(tex_color.a < 0.1 && tex_color.r > 0.1) { // torso pixel
			gl_FragColor.rgb = color.rgb*tex_color.b;
			gl_FragColor.a = color.a;
		} else {
			gl_FragColor = tex_color;
		}
	}
	float fragDepth = gl_FragCoord.z;

	if(uni_info.y > 0.1 && tex_color.g > 0.1 && tex_color.a < 0.1) { // shadow pixel
		gl_FragColor.rgba = tex_color.aaag;
		fragDepth += shadow_depth;
	}

	gl_FragColor.rgb *= uni_info.z;

	if(gl_FragColor.a < 0.5) discard;

	#if 1 //VENDOR=Qualcomm 1=0
	gl_FragDepth = fragDepth;
	#endif
}
