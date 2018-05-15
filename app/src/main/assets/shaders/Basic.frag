#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
in vec3 v2f_norm;

out vec4 out_color;

void main() {
    vec3 norm = normalize(v2f_norm);
    out_color = vec4(norm * 0.5f + 0.5f, 1.0f);
}
