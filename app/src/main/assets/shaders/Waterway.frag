#version 300 es

precision highp float;

in vec3 v2f_loc;
in vec3 v2f_norm;

out vec4 out_color;

uniform vec3 u_lightDir;

const float k_ambience = 0.15f;

void main() {
    vec3 norm = normalize(v2f_norm);

    // Diffuse lighting
    float diffuse = (1.0f - k_ambience) * max(dot(norm, -u_lightDir), 0.0f) + k_ambience;

    out_color.rgb = vec3(diffuse);
    out_color.a = 1.0f;
}
