#version 300 es

precision highp float;

in vec3 v2f_loc;
in vec3 v2f_norm;
in vec2 v2f_pos;
in float v2f_selected;
in vec3 v2f_continentColor;
in float v2f_angle;

out vec4 out_color;

uniform float u_time;
uniform vec3 u_lightDir;

const float k_radToDegFactor = 180.0f / 3.14159265f;
const float k_ambience = 0.15f;

void main() {
    vec3 norm = normalize(v2f_norm);

    // Diffuse lighting
    float diffuse = (1.0f - k_ambience) * max(dot(norm, -u_lightDir), 0.0f) + k_ambience;

    out_color.rgb = mix(v2f_continentColor * diffuse, vec3(1.0f), v2f_selected);
    out_color.a = float(fract(v2f_pos.x * k_radToDegFactor * v2f_angle * 0.25f + (1.0f - u_time * 0.5f)) < 0.5f);
}
