import { useEffect, useState } from "react";
import { getGoods, purchaseGoods } from "./api";
import type { MarketGoodsDto } from "./types";
import { GoodsType } from "./types";

function App() {
  const [goods, setGoods] = useState<MarketGoodsDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchGoods = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getGoods();
      setGoods(data);
    } catch (e) {
      setError("Failed to load goods");
    } finally {
      setLoading(false);
    }
  };

  const handlePurchase = async (type: GoodsType) => {
    try {
      await purchaseGoods(type);
      await fetchGoods();
    } catch (e) {
      setError("Purchase failed");
    }
  };

  useEffect(() => {
    fetchGoods();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;

  return (
      <div style={{ padding: "20px" }}>
        <h1>Market Goods</h1>
        <table style={{ borderCollapse: "collapse", width: "100%" }}>
          <thead>
          <tr>
            <th style={th}>Type</th>
            <th style={th}>Price</th>
            <th style={th}>Quantity</th>
            <th style={th}>Timestamp</th>
            <th style={th}>Action</th>
          </tr>
          </thead>
          <tbody>
          {goods.map((item) => (
              <tr key={item.goodsType}>
                <td style={td}>{item.goodsType}</td>
                <td style={td}>{item.price}</td>
                <td style={td}>{item.quantity}</td>
                <td style={td}>{item.timestamp ?? "-"}</td>
                <td style={td}>
                  <button onClick={() => handlePurchase(item.goodsType)}>
                    Buy
                  </button>
                </td>
              </tr>
          ))}
          </tbody>
        </table>
      </div>
  );
}

const th: React.CSSProperties = {
  border: "1px solid #ccc",
  padding: "8px",
  textAlign: "left",
  background: "#f5f5f5",
};

const td: React.CSSProperties = {
  border: "1px solid #ccc",
  padding: "8px",
};

export default App;