#version 300 es

precision highp float;

in vec2 v2f_texCoord;

out vec4 out_color;

uniform sampler2D u_texture;

const vec3 lightDir = normalize(vec3(1.0, 1.0, 1.0));

void main() {
    //out_color.rgb = texture(u_texture, v2f_texCoord).rgb;
    //out_color.a = 1.0f;

    vec2 psize = 1.0f / vec2(textureSize(u_texture, 0));
    float v0 = texture(u_texture, v2f_texCoord).r;
    float vx = texture(u_texture, vec2(v2f_texCoord.x + psize.x, v2f_texCoord.y)).r;
    float vy = texture(u_texture, vec2(v2f_texCoord.x, v2f_texCoord.y + psize.y)).r;
    vec3 xvec = vec3(psize.x, 0.0f, vx - v0);
    vec3 yvec = vec3(0.0f, psize.y, vy - v0);
    vec3 norm = normalize(cross(xvec, yvec));
    out_color.rgb = vec3(v0);
    out_color.rgb = vec3(dot(norm, lightDir) * 0.5f + 0.5f);
    out_color.rg *= v0;
    //out_color.rg = norm.xy * 0.5f + 0.5f;
    //out_color.b = norm.z * 0.5f + 0.25f;
    out_color.a = 1.0f;
}
