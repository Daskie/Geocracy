#version 300 es

precision highp float;
precision highp int;

in vec2 v2f_texCoord;

out vec4 out_color;

uniform sampler2D u_texture;

void main() {
    out_color.rgb = texture(u_texture, v2f_texCoord).rgb;
    out_color.a = 1.0f;
}
