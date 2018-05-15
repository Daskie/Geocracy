#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_texCoord;

out vec4 out_color;

uniform samplerCube u_cubemap;

void main() {
    out_color.rgb = texture(u_cubemap, v2f_texCoord).rgb;
    out_color.a = 1.0f;
}
