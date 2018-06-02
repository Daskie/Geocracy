#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
in vec3 v2f_norm;
flat in vec3 v2f_playerColor;
in vec3 v2f_worldNorm;

out vec4 out_color;

uniform vec3 u_cameraLoc;
uniform vec3 u_lightDir;

const float k_localAmbience = 0.3f;
const float k_worldAmbience = 0.25f;
const float k_shininess = 16.0f;

void main() {
    vec3 norm = normalize(v2f_norm);
    vec3 view = normalize(u_cameraLoc - v2f_loc);
    vec3 light = -u_lightDir;
    vec3 refl = reflect(-light, norm);

    // Diffuse lighting
    float diffuse = (1.0f - k_localAmbience) * max(dot(norm, -u_lightDir), 0.0f) + k_localAmbience;

    // Specular lighting
    float specular = pow(max(dot(view, refl), 0.0f), k_shininess);

    out_color.rgb = v2f_playerColor * max((diffuse + specular) * dot(normalize(v2f_worldNorm), light), k_worldAmbience);
    out_color.a = 1.0f;
}
