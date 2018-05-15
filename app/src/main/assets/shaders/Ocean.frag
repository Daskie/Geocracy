#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
in vec3 v2f_norm;

out vec4 out_color;

uniform vec3 u_cameraLoc;
uniform vec3 u_lightDir;
uniform samplerCube u_cubemap;

const vec3 k_waterColor = vec3(0.0f, 0.0f, 1.0f);
const float k_ambience = 0.15f;
const float k_shininess = 16.0f;
//const float k_r0 = 1.0f / 49.0f;
//const float k_1_min_r0 = 1.0f - k_r0;

void main() {
    vec3 norm = normalize(v2f_norm);
    vec3 view = normalize(u_cameraLoc - v2f_loc);
    vec3 light = -u_lightDir;
    vec3 refl = reflect(-light, norm);

    // Diffuse lighting
    float diffuse = (1.0f - k_ambience) * max(dot(norm, -u_lightDir), 0.0f) + k_ambience;

    // Specular lighting
    float specular = pow(max(dot(view, refl), 0.0f), k_shininess);

    vec3 waterColor = k_waterColor * diffuse;

    vec3 reflColor = texture(u_cubemap, reflect(-view, norm)).rgb + specular;

    //float term1 = 1.0f - dot(norm, view);
    //float term5 = term1 * term1;
    //term5 *= term5;
    //term5 *= term5 * term1;
    //float r = k_r0 + k_1_min_r0 * term5;
    float r = 1.0f - dot(view, norm);

    out_color.rgb = mix(waterColor, reflColor, r);
    out_color.a = mix(2.0f / 3.0f, 1.0f, r);
}
