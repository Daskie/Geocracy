#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
in vec3 v2f_norm;

layout (location = 0) out vec4 out_color;
layout (location = 1) out int out_id;

uniform vec3 u_cameraLoc;
uniform vec3 u_lightDir;

const vec3 k_waterColor = vec3(0.0f, 0.0f, 1.0f);
const float k_ambience = 0.15f;
const float k_shininess = 16.0f;

void main() {
    vec3 norm = normalize(v2f_norm);
    vec3 view = normalize(u_cameraLoc - v2f_loc);
    vec3 light = -u_lightDir;
    vec3 refl = reflect(-light, norm);
    vec3 halfV = normalize(view + light);

    // Diffuse lighting
    float diffuse = (1.0f - k_ambience) * max(dot(norm, -u_lightDir), 0.0f) + k_ambience;

    // Specular lighting
    float specular = pow(max(dot(view, refl), 0.0f), k_shininess);

    out_color.rgb = k_waterColor * (diffuse + specular);
    out_color.a = 1.0f - dot(view, norm) * 0.33f;

    out_id = 0;
}
