#version 300 es

precision highp float;
precision highp int;

flat in lowp uint v2f_territory;

out lowp uint out_id;

void main() {
    out_id = v2f_territory;
}
