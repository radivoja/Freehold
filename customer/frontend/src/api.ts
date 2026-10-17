import axios from "axios";
import type { MarketGoodsDto } from "./types";
import { GoodsType } from "./types";

const client = axios.create({
    baseURL: "/api/goods",
});

export async function getGoods(): Promise<MarketGoodsDto[]> {
    const response = await client.get<MarketGoodsDto[]>("");
    return response.data;
}

export async function purchaseGoods(type: GoodsType): Promise<MarketGoodsDto | null> {
    const response = await client.post<MarketGoodsDto>(`/${type}/purchase`);
    return response.data;
}