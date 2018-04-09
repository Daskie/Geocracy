#version 300 es

precision highp float;

in vec3 v2f_loc;
in vec3 v2f_norm;

out vec4 out_color;

void main() {
    out_color = vec4(v2f_norm * 0.5f + 0.5f, 1.0f);
}
